/*
 * Copyright (c) 2020 Dzikoysk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.panda_lang.reposilite.auth;

import org.jetbrains.annotations.Nullable;
import org.panda_lang.reposilite.Reposilite;
import org.panda_lang.reposilite.repository.RepositoryService;
import org.panda_lang.utilities.commons.StringUtils;
import org.panda_lang.utilities.commons.function.Option;
import org.panda_lang.utilities.commons.function.Result;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

final class Authenticator {

    private final RepositoryService repositoryService;
    private final TokenService tokenService;
    TokenService getTokenService() { return this.tokenService; }

    public Authenticator(File workingDirectory, RepositoryService repositoryService) {
        this.repositoryService = repositoryService;
        this.tokenService = new TokenService(workingDirectory);
    }

    public Result<Session, String> authByUri(Map<String, String> header, String uri) {
        if (!uri.startsWith("/")) {
            uri = "/" + uri;
        }

        Result<Session, String> authResult = authByHeader(header);

        if (authResult.isErr()) {
            Reposilite.getLogger().debug(authResult.getError());
            return authResult;
        }

        Session session = authResult.get();

        if (!session.hasPermissionTo(uri)) {
            return Result.error("Unauthorized access attempt");
        }

        Reposilite.getLogger().info("AUTH " + session.getToken().getAlias() + " accessed " + uri);
        return authResult;
    }

    public Result<Session, String> authByHeader(Map<String, String> header) {
        String authorization = header.get("Authorization");
        Reposilite.getLogger().debug("Header ---");

        header.forEach((key, value) -> {
            Reposilite.getLogger().debug(key + ": " + value);
        });

        if (authorization == null) {
            return Result.error("Authorization credentials are not specified");
        }

        if (!authorization.startsWith("Basic")) {
            return Result.error("Unsupported auth method");
        }

        String base64Credentials = authorization.substring("Basic".length()).trim();
        String credentials = new String(Base64.getDecoder().decode(base64Credentials), StandardCharsets.UTF_8);

        return authByCredentials(credentials);
    }

    public Result<Session, String> authByCredentials(@Nullable String credentials) {
        if (credentials == null) {
            return Result.error("Authorization credentials are not specified");
        }

        String[] values = StringUtils.split(credentials, ":");

        if (values.length != 2) {
            return Result.error("Invalid authorization credentials");
        }

        Option<Token> tokenValue = tokenService.getToken(values[0]);

        if (tokenValue.isEmpty()) {
            return Result.error("Invalid authorization credentials");
        }

        Token token = tokenValue.get();
        boolean authorized = TokenService.B_CRYPT_TOKENS_ENCODER.matches(values[1], token.getToken());

        if (!authorized) {
            return Result.error("Invalid authorization credentials");
        }

        return Result.ok(new Session(token, repositoryService.getRepositories(token)));
    }

}
