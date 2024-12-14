package com.example.demo.services.impl;

import com.example.demo.models.Address;
import com.example.demo.repositories.AddressRepository;
import com.example.demo.services.IAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class AddressServiceImpl implements IAddress {

    @Autowired
    private AddressRepository addressRepository;

    @Override
    public Address saveAddress(Address address) {
        return addressRepository.save(address); // Hibernate tự tạo ID
    }

    @Override
    public Address createAddress(String city, String street, String zipcode) {
        Address address = new Address();
        address.setCity(city);
        address.setStreet(street);
        address.setZipcode(zipcode);
        address.setAddressId(generateUniqueId()); // Sinh ID duy nhất

        return addressRepository.save(address); // Lưu vào cơ sở dữ liệu
    }

    @Override
    public Address updateAddress(Long id, Address updatedAddress) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        address.setCity(updatedAddress.getCity());
        address.setStreet(updatedAddress.getStreet());
        address.setZipcode(updatedAddress.getZipcode());
        address.setCountry(updatedAddress.getCountry());
        address.setNumber(updatedAddress.getNumber());

        return addressRepository.save(address);
    }

    @Override
    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }

    @Override
    public Address getAddressById(Long id) {
        return addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));
    }

    private Long generateUniqueId() {
        return System.currentTimeMillis();
    }
}
