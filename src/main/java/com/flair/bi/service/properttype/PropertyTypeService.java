package com.flair.bi.service.properttype;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.flair.bi.domain.propertytype.PropertyType;

public interface PropertyTypeService {

	Page<PropertyType> findAll(Pageable pageable);

	PropertyType findById(Long id);

	PropertyType save(PropertyType type);

	void delete(long id);
}
