package io.github.hpsocket.soa.framework.logging.gelf.standalone;

import java.util.HashMap;
import java.util.Map;

import io.github.hpsocket.soa.framework.logging.gelf.GelfMessageBuilder;
import io.github.hpsocket.soa.framework.logging.gelf.intern.GelfMessage;
import io.github.hpsocket.soa.framework.logging.gelf.intern.GelfSender;
import io.github.hpsocket.soa.framework.logging.gelf.intern.GelfSenderConfiguration;
import io.github.hpsocket.soa.framework.logging.gelf.intern.GelfSenderFactory;

/**
 * Default implementation of {@link Datenpumpe}.
 *
 * @author Mark Paluch
 * @since 31.07.14 08:47
 */
public class DatenpumpeImpl implements Datenpumpe {

    private final GelfSenderConfiguration gelfSenderConfiguration;

    private final Object mutex = new Object();

    private volatile GelfSender gelfSender = null;

    public DatenpumpeImpl(GelfSenderConfiguration gelfSenderConfiguration) {
        this.gelfSenderConfiguration = gelfSenderConfiguration;
    }

    @Override
    public void submit(Map<String, Object> data) {
        if (data == null) {
            throw new IllegalArgumentException("Data map must not be null");
        }
        Map<String, String> fields = new HashMap<>();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getKey() == null || entry.getValue() == null) {
                continue;
            }

            fields.put(entry.getKey(), entry.getValue().toString());
        }

        GelfMessage gelfMessage = GelfMessageBuilder.newInstance().withJavaTimestamp(System.currentTimeMillis())
                .withFields(fields).build();
        submit(gelfMessage);
    }

    @Override
    public void submit(GelfMessage gelfMessage) {
        if (gelfMessage == null) {
            throw new IllegalArgumentException("GelfMessage must not be null");
        }

        if (gelfSender == null) {
            synchronized (mutex) {
                if (gelfSender == null) {
                    gelfSender = GelfSenderFactory.createSender(gelfSenderConfiguration);
                }
            }
        }

        gelfSender.sendMessage(gelfMessage);

    }

    @Override
    public void submit(Object javaBean) {
        if (javaBean == null) {
            throw new IllegalArgumentException("Passed object must not be null");
        }

        Map<String, Object> fields = BeanPropertyExtraction.extractProperties(javaBean);

        submit(fields);
    }

    public void close() {
        if (gelfSender != null) {
            synchronized (mutex) {
                if (gelfSender != null) {
                    gelfSender.close();
                    gelfSender = null;
                }
            }
        }
    }
}
