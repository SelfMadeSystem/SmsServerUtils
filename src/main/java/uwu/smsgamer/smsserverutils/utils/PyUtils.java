package uwu.smsgamer.smsserverutils.utils;

import uwu.smsgamer.senapi.utils.Evaluator;

import java.util.concurrent.*;

public class PyUtils {
    public static ScriptReturn exec(String command, int timeoutSec, String... stuff) {
        final Callable<Object> c = () -> Evaluator.evaluateWithParam(command, stuff);
        final Future<Object> f = Executors.newCachedThreadPool().submit(c);
        try {
            return new ScriptReturn(f.get(timeoutSec, TimeUnit.SECONDS));
        } catch (InterruptedException e) {
            return new ScriptReturn(ScriptReturn.Type.INTERRUPTED);
        } catch (ExecutionException e) {
            return new ScriptReturn(ScriptReturn.Type.EXCEPTION, e.getCause());
        } catch (TimeoutException e) {
            return new ScriptReturn(ScriptReturn.Type.TIMEOUT);
        }
    }
}
