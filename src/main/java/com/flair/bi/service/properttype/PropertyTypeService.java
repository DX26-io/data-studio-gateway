package com.flair.bi.service.properttype;

import com.flair.bi.domain.propertytype.PropertyType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PropertyTypeService {

	Page<PropertyType> findAll(Pageable pageable);

	PropertyType findById(Long id);

	PropertyType save(PropertyType type);

	void delete(long id);

	Optional<PropertyType> findByName(String name);
}
