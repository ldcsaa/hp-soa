
package io.github.hpsocket.soa.framework.core.log;

import java.util.regex.Pattern;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

import io.github.hpsocket.soa.framework.core.util.GeneralHelper;

/** <b>log4j2 logger 日志过滤器</b><br>
 * 用于调整 logger 名称匹配特定正则表达式的 logger 的日志级别
 */
@Plugin(name = "LoggerNameFilter", category = Node.CATEGORY, elementType = Filter.ELEMENT_TYPE, printObject = true)
public class LoggerNameFilter extends AbstractFilter
{
    private final Level level;
    private final Pattern name;

    private LoggerNameFilter(final Pattern name, final Level level, final Result onMatch, final Result onMismatch)
    {
        super(onMatch, onMismatch);
        this.level = level;
        this.name = name;
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final String msg, final Object... params)
    {
        return filter(logger.getName(), level);
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final Object msg, final Throwable t)
    {
        return filter(logger.getName(), level);
    }

    @Override
    public Result filter(final Logger logger, final Level level, final Marker marker, final Message msg, final Throwable t)
    {
        return filter(logger.getName(), level);
    }

    @Override
    public Result filter(final LogEvent event)
    {
        return filter(event.getLoggerName(), event.getLevel());
    }

    private Result filter(String name, final Level level)
    {
        if(level.isLessSpecificThan(this.level) && this.name.matcher(name).matches())
            return onMismatch;

        return onMatch;
    }

    public Level getLevel()
    {
        return level;
    }

    public String getName()
    {
        return name.toString();
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder();
        
        sb.append("level=").append(level);
        sb.append(", name=").append(name);
        
        return sb.toString();
    }

    @PluginFactory
    public static LoggerNameFilter createFilter(
                    @PluginAttribute("name") final String name,
                    @PluginAttribute("level") final Level level,
                    @PluginAttribute("onMatch") final Result match,
                    @PluginAttribute("onMismatch") final Result mismatch)
    {
        if(GeneralHelper.isStrEmpty(name))
        {
            LOGGER.error("'name' attribute must be provided for LoggerNameFilter");
            return null;
        }

        final Level actualLevel    = level == null ? Level.ERROR : level;
        final Result onMatch    = match == null ? Result.NEUTRAL : match;
        final Result onMismatch    = mismatch == null ? Result.DENY : mismatch;
        
        return new LoggerNameFilter(Pattern.compile(name), actualLevel, onMatch, onMismatch);
    }
}
