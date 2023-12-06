package io.github.hpsocket.soa.framework.logging.gelf;

/**
 * @author Mark Paluch
 */
public class StaticMessageField implements MessageField {

    private String name;
    private String value;

    public StaticMessageField(String name, String value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [name='").append(name).append('\'');
        sb.append(", value='").append(value).append('\'');
        sb.append(']');
        return sb.toString();
    }
}
