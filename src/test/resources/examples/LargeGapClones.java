// Large-gap clone example from CCAligner's paper
class LargeGapClones {
    int method1(Commandline cmd) {
        try {
            Execute exe = new Execute(new LogStreamHandler(this, Project.MSG_INFO, Project.MSG_WARN));
            exe.setAntRun(getProject());
            exe.setWorkingDirectory(getProject().getBaseDir());
            exe.setCommandline(cmd.getCommandline());
            exe.setVMLauncher(false);
            return exe.execute();
        } catch (java.io.IOException e) {
            throw new BuildExecption(e, getLocatin());
        }
    }

    int method2(Commandline cmd) {
        try {
            Execute exe = new Execute(new LogStreamHandler(this, Project.MSG_INFO, Project.MSG_WARN));
            // Gap is from here
            if (serverPath != null) {
                String[] env = exe.getEnvironment();
                if (env == null) {
                    env = new String[0];
                }
                String[] newEnv = new String[env.length + 1];
                System.arraycopy(env, 0, newEnv, env.length);
                newEnv[env.length] = "SSDIR=" + servePath;
                exe.setEnvironment(newEnv);
            }
            // to here
            exe.setAntRun(getProject());
            exe.setWorkingDirectory(getProject().getBaseDir());
            exe.setCommandline(cmd.getCommandline());
            exe.setVMLauncher(false);
            return exe.execute();
        } catch (java.io.IOException e) {
            throw new BuildExecption(e, getLocatin());
        }
    }
}