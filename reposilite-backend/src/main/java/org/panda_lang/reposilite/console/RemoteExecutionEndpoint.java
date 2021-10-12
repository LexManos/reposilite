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

package org.panda_lang.reposilite.console;

import io.javalin.http.Context;
import io.javalin.plugin.openapi.annotations.HttpMethod;
import io.javalin.plugin.openapi.annotations.OpenApi;
import io.javalin.plugin.openapi.annotations.OpenApiContent;
import io.javalin.plugin.openapi.annotations.OpenApiParam;
import io.javalin.plugin.openapi.annotations.OpenApiResponse;
import org.apache.http.HttpStatus;
import org.panda_lang.reposilite.Reposilite;
import org.panda_lang.reposilite.ReposiliteContext;
import org.panda_lang.reposilite.auth.IAuthManager;
import org.panda_lang.reposilite.auth.IAuthedHandler;
import org.panda_lang.reposilite.auth.Session;
import org.panda_lang.reposilite.error.ErrorDto;
import org.panda_lang.reposilite.error.ResponseUtils;
import org.panda_lang.utilities.commons.StringUtils;
import org.panda_lang.utilities.commons.function.Result;

import java.util.List;

public final class RemoteExecutionEndpoint implements IAuthedHandler {

    private static final int MAX_COMMAND_LENGTH = 1024;

    private final IAuthManager auth;
    private final Console console;

    public RemoteExecutionEndpoint(IAuthManager auth, Console console) {
        this.auth = auth;
        this.console = console;
    }

    @OpenApi(
        operationId = "cli",
        method = HttpMethod.POST,
        summary = "Remote command execution",
        description = "Execute command using POST request. The commands are the same as in the console and can be listed using the 'help' command.",
        tags = { "Cli" },
        headers = {
            @OpenApiParam(name = "Authorization", description = "Alias and token provided as basic auth credentials", required = true)
        },
        responses = {
            @OpenApiResponse(status = "200", description = "Status of the executed command", content = {
                @OpenApiContent(from = RemoteExecutionDto.class)
            }),
            @OpenApiResponse(
                status = "400",
                description = "Error message related to the invalid command format (0 < command length < " + MAX_COMMAND_LENGTH + ")",
                content = @OpenApiContent(from = ErrorDto.class)
            ),
            @OpenApiResponse(status = "401", description = "Error message related to the unauthorized access", content = {
                @OpenApiContent(from = ErrorDto.class)
            })
        }
    )
    @Override
    public void handle(Context ctx, ReposiliteContext context) {
        Reposilite.getLogger().info("REMOTE EXECUTION " + context.uri() + " from " + context.address());

        Result<Session, String> authResult = auth.getSession(context.headers(), null);

        if (authResult.isErr()) {
            ResponseUtils.errorResponse(ctx, HttpStatus.SC_UNAUTHORIZED, authResult.getError());
            return;
        }

        Session session = authResult.get();

        if (!session.isManager()) {
            ResponseUtils.errorResponse(ctx, HttpStatus.SC_UNAUTHORIZED, "Authenticated user is not a manger");
            return;
        }

        String command = ctx.body();

        if (StringUtils.isEmpty(command)) {
            ResponseUtils.errorResponse(ctx, HttpStatus.SC_BAD_REQUEST, "Missing command");
            return;
        }

        if (command.length() > MAX_COMMAND_LENGTH) {
            ResponseUtils.errorResponse(ctx, HttpStatus.SC_BAD_REQUEST, "The given command exceeds allowed length (" + command.length() + " > " + MAX_COMMAND_LENGTH + ")");
            return;
        }

        Reposilite.getLogger().info(session.getAlias() + " (" + context.address() + ") requested command: " + command);
        Result<List<String>, List<String>> result = console.execute(command);

        ctx.json(new RemoteExecutionDto(result.isOk(), result.isOk() ? result.get() : result.getError()));
    }

}
