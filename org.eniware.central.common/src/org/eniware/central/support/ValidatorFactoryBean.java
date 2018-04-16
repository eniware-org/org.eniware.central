/* ==================================================================
 *  Eniware Open sorce:Nikolai Manchev
 *  Apache License 2.0
 * ==================================================================
 */

package org.eniware.central.support;

import java.util.ArrayList;
import java.util.List;
import javax.validation.ConstraintValidatorFactory;
import javax.validation.MessageInterpolator;
import javax.validation.ParameterNameProvider;
import javax.validation.TraversableResolver;
import javax.validation.Validation;
import javax.validation.ValidationProviderResolver;
import javax.validation.Validator;
import javax.validation.ValidatorContext;
import javax.validation.ValidatorFactory;
import javax.validation.executable.ExecutableValidator;
import javax.validation.spi.ValidationProvider;
import org.hibernate.validator.HibernateValidator;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;

/**
 * Bootstrap javax.validation for OSGi using {@link HibernateValidator}.
 * 
 * 
 * @version 1.0
 */
public final class ValidatorFactoryBean {

	private static class SpringValidatorFactoryBean extends SpringValidatorAdapter implements
			ValidatorFactory {

		public SpringValidatorFactoryBean(ValidatorFactory validatorFactory) {
			super(validatorFactory.getValidator());
			this.validatorFactory = validatorFactory;
		}

		private final ValidatorFactory validatorFactory;

		@Override
		public Validator getValidator() {
			return this.validatorFactory.getValidator();
		}

		@Override
		public ValidatorContext usingContext() {
			return this.validatorFactory.usingContext();
		}

		@Override
		public MessageInterpolator getMessageInterpolator() {
			return this.validatorFactory.getMessageInterpolator();
		}

		@Override
		public TraversableResolver getTraversableResolver() {
			return this.validatorFactory.getTraversableResolver();
		}

		@Override
		public ConstraintValidatorFactory getConstraintValidatorFactory() {
			return this.validatorFactory.getConstraintValidatorFactory();
		}

		@Override
		public ExecutableValidator forExecutables() {
			return null; // TODO: where does this come from?
		}

		@Override
		public void close() {
			this.validatorFactory.close();
		}

		@Override
		public ParameterNameProvider getParameterNameProvider() {
			// TODO Auto-generated method stub
			return null;
		}

	}

	private static class OsgiServiceDiscoverer implements ValidationProviderResolver {

		@Override
		public List<ValidationProvider<?>> getValidationProviders() {
			List<ValidationProvider<?>> list = new ArrayList<ValidationProvider<?>>();
			list.add(new org.hibernate.validator.HibernateValidator());
			return list;
		}
	}

	private final static ValidatorFactory instance;

	static {
		instance = new SpringValidatorFactoryBean(Validation.byDefaultProvider()
				.providerResolver(new OsgiServiceDiscoverer()).configure().buildValidatorFactory());
	}

	public final static ValidatorFactory getInstance() {
		return instance;
	}
}
