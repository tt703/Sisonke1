package com.example.tlotlotau.Documents.Invoice;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.tlotlotau.Customer.Customer;
import com.example.tlotlotau.Documents.Item;

import java.util.ArrayList;

public class EstimateViewModel  extends ViewModel {
    private final MutableLiveData<ArrayList<Item>> items = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Customer> customer = new MutableLiveData<>();

    public  MutableLiveData<ArrayList<Item>> getItems(){
        return items;
    }

    public  MutableLiveData<Customer> getCustomer(){
        return customer;
    }

    public void addItem(Item item){
        ArrayList<Item> currentItems = items.getValue();
        if (currentItems != null){
            currentItems.add(item);
            items.setValue(currentItems);
        }
    }

    public void setCustomer(Customer customerInfo){
        customer.setValue(customerInfo);
    }
}


