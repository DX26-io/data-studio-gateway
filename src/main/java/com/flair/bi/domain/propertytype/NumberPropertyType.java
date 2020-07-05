package com.flair.bi.domain.propertytype;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.fasterxml.jackson.annotation.JsonTypeName;

@Entity
@DiscriminatorValue(value = "NUMBER")
@JsonTypeName(value = "NUMBER")
public class NumberPropertyType extends PropertyType {

	@Column(name = "number_default_value")
	private int defaultValue;

	public NumberPropertyType() {
		this.setType("NUMBER");
	}

	public int getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(int defaultValue) {
		this.defaultValue = defaultValue;
	}
}
