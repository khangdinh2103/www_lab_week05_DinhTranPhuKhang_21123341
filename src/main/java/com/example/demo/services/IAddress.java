package com.example.demo.services;

import com.example.demo.models.Address;

public interface IAddress {
    Address saveAddress(Address address);
    Address createAddress(String city, String street, String zipcode);
    Address updateAddress(Long id, Address updatedAddress);
    void deleteAddress(Long id);
    Address getAddressById(Long id);
}
