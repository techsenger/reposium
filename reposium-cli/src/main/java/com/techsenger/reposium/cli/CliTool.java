/*
 * Copyright 2018-2025 Pavel Castornii.
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

package com.techsenger.reposium.cli;

import com.beust.jcommander.JCommander;
import com.techsenger.reposium.cli.commands.CommandFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Pavel Castornii
 */
public final class CliTool {

    private static final Logger logger = LoggerFactory.getLogger(CliTool.class);

    public static void main(String[] args) {
        String[] argsWithoutCommand = new String[args.length - 1];
        System.arraycopy(args, 1, argsWithoutCommand, 0, argsWithoutCommand.length);
        var command = CommandFactory.instance(args[0]);
        //parsing arguments
        JCommander.newBuilder()
                .addObject(command)
                .build()
                .parse(argsWithoutCommand);
        //executing command
        command.execute();
    }

    private CliTool() {
        //empty
    }
}
