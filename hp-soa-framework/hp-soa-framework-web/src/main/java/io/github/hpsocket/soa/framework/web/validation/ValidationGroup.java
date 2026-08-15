package io.github.hpsocket.soa.framework.web.validation;

import jakarta.validation.groups.Default;

public interface ValidationGroup
{
	interface Create extends Default	{}
	interface Update extends Default {}
	interface CreateOrUpdate extends Default {}
	interface Delete extends Default {}

	interface RequireId extends Default {}
	interface OptionalId extends Default {}
	interface NullId extends Default {}
}
