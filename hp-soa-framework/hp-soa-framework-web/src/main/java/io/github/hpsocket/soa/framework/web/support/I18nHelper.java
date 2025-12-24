package io.github.hpsocket.soa.framework.web.support;

import java.util.Locale;
import java.util.regex.Pattern;

import org.slf4j.MDC;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.util.StringUtils;

import io.github.hpsocket.soa.framework.core.mdc.MdcAttr;
import io.github.hpsocket.soa.framework.core.util.GeneralHelper;
import io.github.hpsocket.soa.framework.web.holder.SpringContextHolder;
import jakarta.servlet.http.HttpServletRequest;

/** <b>I18n 辅助类</b> */
public class I18nHelper
{
    private static final Pattern MESSAGE_KEY_PATTERN = Pattern.compile("^\\{[a-zA-Z0-9][a-zA-Z0-9\\-_.]+[a-zA-Z0-9]\\}$");

    private static MessageSource defaultMessageSource;
    
    public static final Locale getLocaleByMdcOrRequest(HttpServletRequest request)
    {
        Locale mdcLocale = getMdcLocale();        
        return mdcLocale != null ? mdcLocale : request.getLocale();
    }

    public static final Locale getMdcLocaleOrDefault()
    { 
        return getMdcLocaleOrDefault(Locale.getDefault());
    }
    
    public static final Locale getMdcLocaleOrDefault(Locale defaultLocale)
    {
        Locale mdcLocale = getMdcLocale();        
        return mdcLocale != null ? mdcLocale : defaultLocale;
    }
    
    public static final Locale getMdcLocale()
    {
        String language = MDC.get(MdcAttr.MDC_LANGUAGE_KEY);
        
        if(GeneralHelper.isStrNotEmpty(language))
            return StringUtils.parseLocale(language);
        
        return null;
    }

    public static final String getMessage(MessageSource messageSource, String code, Object[] args, String defaultMessage)
    {
        return messageSource.getMessage(code, args, defaultMessage, getMdcLocaleOrDefault());
    }

    public static final String getMessage(MessageSource messageSource, String code, Object ... args)
    {
        return messageSource.getMessage(code, args, getMdcLocaleOrDefault());
    }

    public static final String getMessage(MessageSource messageSource, MessageSourceResolvable resolvable)
    {
        return messageSource.getMessage(resolvable, getMdcLocaleOrDefault());
    }

    public static final String getMessage(String code, Object[] args, String defaultMessage)
    {
        MessageSource messageSource = getDefaultMessageSource();
        return getMessage(messageSource, code, args, defaultMessage);
    }

    public static final String getMessage(String code, Object ... args)
    {
        MessageSource messageSource = getDefaultMessageSource();
        return getMessage(messageSource, code, args);
    }

    public static final String getMessage(MessageSourceResolvable resolvable)
    {
        MessageSource messageSource = getDefaultMessageSource();
        return getMessage(messageSource, resolvable);
    }

    public static final String ensureI18nMessage(String message, Object ... args) {
        if(GeneralHelper.isStrEmpty(message)) {
            return message;
        }

        if(MESSAGE_KEY_PATTERN.matcher(message).matches()) {
            String code = message.substring(1, message.length() - 1);
            return getMessage(getDefaultMessageSource(), code, args);
        }

        if(args != null && args.length > 0) {
            return String.format(message, args);
        }

        return message;
    }
    
    private static final MessageSource getDefaultMessageSource()
    {
        if(defaultMessageSource == null)
        {
            synchronized(I18nHelper.class)
            {
                if(defaultMessageSource == null)
                {
                    defaultMessageSource = SpringContextHolder.getBean(MessageSource.class);
                }
            }
        }
        
        return defaultMessageSource;
    }
}
