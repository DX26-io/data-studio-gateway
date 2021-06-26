package com.flair.bi.service.properttype;

import com.flair.bi.domain.listeners.PropertyTypeListener;
import com.flair.bi.domain.propertytype.PropertyType;
import com.flair.bi.domain.propertytype.QPropertyType;
import com.flair.bi.domain.propertytype.SelectPropertyType;
import com.flair.bi.repository.PropertyTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PropertyTypeServiceImpl implements PropertyTypeService {

	private final PropertyTypeRepository propertyTypeRepository;

	@Override
	@Transactional
	public Page<PropertyType> findAll(Pageable pageable) {
		return propertyTypeRepository.findAll(pageable);
	}

	@Override
	@Transactional
	public PropertyType findById(Long id) {
		final PropertyType propertyType = propertyTypeRepository.getOne(id);

		if (propertyType instanceof SelectPropertyType) {
			((SelectPropertyType) propertyType).getPossibleValues().size();// fetch possible values
		}
		return propertyType;

	}

	@PreAuthorize("@accessControlManager.hasAccess('REALM-MANAGEMENT', 'WRITE', 'APPLICATION')")
	@Override
	public PropertyType save(PropertyType type) {
		type = PropertyTypeListener.rewireRelationships(type);
		return propertyTypeRepository.save(type);
	}

	@PreAuthorize("@accessControlManager.hasAccess('REALM-MANAGEMENT', 'WRITE', 'APPLICATION')")
	@Override
	public void delete(long id) {
		propertyTypeRepository.deleteById(id);
	}

	@Override
	public Optional<PropertyType> findByName(String name) {
		return propertyTypeRepository.findOne(QPropertyType.propertyType.name.equalsIgnoreCase(name));
	}
}
