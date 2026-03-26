package com.hospital.hospital.common.mapper;

import org.springframework.stereotype.Component;

import com.hospital.hospital.common.dto.AddressDto;
import com.hospital.hospital.common.dto.CityDto;
import com.hospital.hospital.common.dto.ContactDto;
import com.hospital.hospital.common.dto.CountryDto;
import com.hospital.hospital.common.dto.PhoneDto;
import com.hospital.hospital.common.model.Address;
import com.hospital.hospital.common.model.City;
import com.hospital.hospital.common.model.Contact;
import com.hospital.hospital.common.model.Country;
import com.hospital.hospital.common.model.Phone;

// Ortak value object dönüşümlerini manuel olarak yönetir.
@Component
public class CommonValueObjectMapper {

	public Phone toEntity(PhoneDto dto) {
		if (dto == null) {
			return null;
		}
		return new Phone(dto.getCountryCode(), dto.getNumber());
	}

	public PhoneDto toDto(Phone entity) {
		if (entity == null) {
			return null;
		}
		return new PhoneDto(entity.getCountryCode(), entity.getNumber());
	}

	public Country toEntity(CountryDto dto) {
		if (dto == null) {
			return null;
		}
		return new Country(dto.getCode(), dto.getName());
	}

	public CountryDto toDto(Country entity) {
		if (entity == null) {
			return null;
		}
		return new CountryDto(entity.getCode(), entity.getName());
	}

	public City toEntity(CityDto dto) {
		if (dto == null) {
			return null;
		}
		return new City(dto.getCode(), dto.getName());
	}

	public CityDto toDto(City entity) {
		if (entity == null) {
			return null;
		}
		return new CityDto(entity.getCode(), entity.getName());
	}

	public Contact toEntity(ContactDto dto) {
		if (dto == null) {
			return null;
		}
		return new Contact(toEntity(dto.getPhone()), dto.getEmail());
	}

	public ContactDto toDto(Contact entity) {
		if (entity == null) {
			return null;
		}
		return new ContactDto(toDto(entity.getPhone()), entity.getEmail());
	}

	public Address toEntity(AddressDto dto) {
		if (dto == null) {
			return null;
		}
		return new Address(
				toEntity(dto.getCountry()),
				toEntity(dto.getCity()),
				dto.getDistrict(),
				dto.getPostalCode(),
				dto.getAddressLine());
	}

	public AddressDto toDto(Address entity) {
		if (entity == null) {
			return null;
		}
		return new AddressDto(
				toDto(entity.getCountry()),
				toDto(entity.getCity()),
				entity.getDistrict(),
				entity.getPostalCode(),
				entity.getAddressLine());
	}
}
