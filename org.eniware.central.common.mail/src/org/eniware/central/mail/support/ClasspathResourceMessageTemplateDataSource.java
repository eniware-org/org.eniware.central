/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.mail.support;

import java.util.Locale;
import java.util.Map;

import org.eniware.central.mail.MessageTemplateDataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

/**
 * {@link MessageTemplateDataSource} based on a locale-specific classpath
 * resource.
 * 
 * <p>
 * The {@link #getMessageTemplate()} will load a classpath resource located at
 * the {@code resource} path passed to the class constructor. The resource path
 * must have a file extension, and first this method will insert
 * <code>_<em>lang</em></code> before the file extension and attempt to use that
 * resource, where <em>lang</em> is the language value returned by
 * {@link Locale#getLanguage()} on the {@code Locale} object passed to the class
 * constructor.
 * </p>
 * 
 * <p>
 * If the language-specific resource is not found, it will try to use the
 * resource path exactly as configured. If that resource cannot be found, a
 * {@code RuntimeException} will be thrown.
 * </p>
 * 
 * @author matt
 * @version 1.1
 */
public class ClasspathResourceMessageTemplateDataSource implements MessageTemplateDataSource {

	private final Locale locale;
	private final String subject;
	private final String resource;
	private final Map<String, ?> model;
	private ClassLoader classLoader;
	private Integer wordWrapCharacterIndex;

	/**
	 * Construct with values.
	 * 
	 * @param locale
	 *        the locale to use when locating the message resource
	 * @param subject
	 *        the subject to use
	 * @param resource
	 *        the resource path to the message template
	 * @param model
	 *        the mail merge model to use
	 */
	public ClasspathResourceMessageTemplateDataSource(Locale locale, String subject, String resource,
			Map<String, ?> model) {
		this.locale = locale;
		this.subject = subject;
		this.resource = resource;
		this.model = model;
	}

	@Override
	public Locale getLocale() {
		return locale;
	}

	@Override
	public Resource getMessageTemplate() {
		// first try via locale lang
		String resourcePath = StringUtils.stripFilenameExtension(resource) + '_'
				+ this.locale.getLanguage() + '.' + StringUtils.getFilenameExtension(resource);
		ClassLoader loader = classLoader;
		if ( loader == null ) {
			try {
				loader = Thread.currentThread().getContextClassLoader();
			} catch ( Throwable ex ) {
				// ignore
			}
			if ( loader == null ) {
				loader = getClass().getClassLoader();
			}
		}
		if ( loader.getResource(resourcePath) == null ) {
			// try without lang
			resourcePath = this.resource;
			if ( loader.getResource(resourcePath) == null ) {
				throw new RuntimeException("Resource [" + this.resource + "] not available.");
			}
		}
		return new ClassPathResource(resourcePath, loader);
	}

	@Override
	public Map<String, ?> getModel() {
		return model;
	}

	@Override
	public String getSubject() {
		return subject;
	}

	/**
	 * Get the custom ClassLoader to use when resolving the template resource.
	 * 
	 * @return classLoader The ClassLoader to use.
	 * @since 1.1
	 */
	public ClassLoader getClassLoader() {
		return classLoader;
	}

	/**
	 * Set a custom ClassLoader to use when resolving the template resource.
	 * 
	 * @param classLoader
	 *        The ClassLoader to use.
	 * @since 1.1
	 */
	public void setClassLoader(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}

	@Override
	public Integer getWordWrapCharacterIndex() {
		return wordWrapCharacterIndex;
	}

	/**
	 * Set the word wrap character index.
	 * 
	 * @param wordWrapCharacterIndex
	 *        The word wrap character index to set.
	 * @see MessageTemplateDataSource#getWordWrapCharacterIndex()
	 * @since 1.1
	 */
	public void setWordWrapCharacterIndex(Integer wordWrapCharacterIndex) {
		this.wordWrapCharacterIndex = wordWrapCharacterIndex;
	}

}
