package io.github.hpsocket.soa.framework.logging.gelf.intern;

/**
 * {@link ErrorReporter} that post-processes the error message if it is {@code null} by using the exception class name as
 * fallback.
 *
 * @author Mark Paluch
 * @since 1.11.2
 */
public class MessagePostprocessingErrorReporter implements ErrorReporter {

    private final ErrorReporter delegate;

    public MessagePostprocessingErrorReporter(ErrorReporter delegate) {
        this.delegate = delegate;
    }

    @Override
    public void reportError(String message, Exception e) {

        String messageToUse = postProcessMessage(message, e);

        delegate.reportError(messageToUse, e);
    }

    private static String postProcessMessage(String message, Exception e) {

        if(message == null || message.isBlank() || "null".equalsIgnoreCase(message)) {
            if(e == null) {
                return "unknown send message error";
            }
            
            return e.getMessage();
        }
        
        return message + (e != null ? " - " + e.getMessage() : "");
    }
}
