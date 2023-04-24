package com.example.onlinewaiter.Other;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.onlinewaiter.employeeUI.menu.MenuViewModel;

public class FirebaseRefPaths {
    /*appErrors*/
    private final String refAppErrors = "appErrors/";


    /*appInfo*/
    private final String refAppInfo = "appInfo/";


    /*cafes*/
    private final String refCafes = "cafes/";
    private final String refCafeCategories = "/cafeDrinksCategories/";
    private final String refCategoryDrinks = "/categoryDrinks/";
    private final String refCafeBills = "/cafeBills/";
    private final String refCafeCurrentOrders = "/cafeCurrentOrders/";
    private final String refCafeCategoryName = "/cafeCategoryName/";
    private final String refSingleCafeCategoryName = "cafeCategoryName";
    private final String refCafeCategoryDescription = "/cafeCategoryDescription/";
    private final String refSingleCafeCategoryDescription = "cafeCategoryDescription";
    private final String refCafeCategoryImage = "/cafeCategoryImage/";
    private final String refSingleCafeCategoryImage = "cafeCategoryImage";
    private final String refCategoryDrinkImage = "/categoryDrinkImage";
    private final String refCafeCategoriesChild = "cafeDrinksCategories";
    private final String refCafeTablesChild = "cafeTables";
    private final String refCafeTables = "/cafeTables/";
    private final String refCategoryDrinksChild = "categoryDrinks";

    /*countriesNumber*/



    /*drinksCategories*/
    private final String refDrinksCategories = "drinksCategories/";


    /*registeredNumbers*/
    private final String refRegisteredNumbers = "registeredNumbers/";
    private final String refRegisteredNumberWaiter = "waiter";
    private final String refRegisteredNumberOwner = "owner";



    /*firebase storage*/
    private final String storageCategoryDrinksNoImage = "https://firebasestorage.googleapis.com/v0/b/online-waiter-db1c0.appspot.com/o/appImages%2Fstandard%2Fno_image.jpg?alt=media&token=34103017-4678-451f-acb2-6247186c6d07";
    private final String storageCategoryDrinks = "categoryDrinks/";
    private final String storageCategories = "categories/";
    private final String storageCountryFlags = "countryFlags/";

    /*Property*/
    ViewModelStoreOwner viewModelStoreOwner;
    private MenuViewModel menuViewModel;


    public FirebaseRefPaths() {

    }

    public FirebaseRefPaths(ViewModelStoreOwner viewModelStoreOwner) {
        this.viewModelStoreOwner = viewModelStoreOwner;
        menuViewModel= new ViewModelProvider(viewModelStoreOwner).get(MenuViewModel.class);
    }

    /*Methods*/
    /*appError*/
    public String getRefAppErrors() {
        return refAppErrors;
    }

    /*appInfo*/
    public String getRefAppInfo() {
        return refAppInfo;
    }

    /*cafes*/
    public String getRefCafes() {
        return refCafes;
    }

    public String getRefCafe() {
        return refCafes + menuViewModel.getCafeId().getValue();
    }

    public String getRefCafeCategories() {
        return getRefCafe() + refCafeCategories;
    }

    public String getRefCafeCategory(String cafeCategoryId) {
        return getRefCafeCategories() + cafeCategoryId;
    }

    public String getRefCafeCategoryName(String cafeCategoryId) {
        return getRefCafeCategory(cafeCategoryId) + refCafeCategoryName;
    }

    public String getRefCafeCategoryDescription(String cafeCategoryId) {
        return getRefCafeCategory(cafeCategoryId) + refCafeCategoryDescription;
    }

    public String getRefCafeCategoryImage(String cafeCategoryId) {
        return getRefCafeCategory(cafeCategoryId) + refCafeCategoryImage;
    }

    public String getRefCategoryDrinks(String cafeCategoryId) {
        return getRefCafeCategory(cafeCategoryId) + refCategoryDrinks;
    }

    public String getRefCategoryDrink(String cafeCategoryId, String categoryDrinkId) {
        return getRefCategoryDrinks(cafeCategoryId) + categoryDrinkId;
    }

    public String getCategoryDrinkImage(String cafeCategoryId, String categoryDrinkId) {
        return getRefCategoryDrink(cafeCategoryId, categoryDrinkId) + refCategoryDrinkImage;
    }

    public String getRefSingleCafeCategoryName() {
        return refSingleCafeCategoryName;
    }

    public String getRefSingleCafeCategoryImage() {
        return refSingleCafeCategoryImage;
    }

    public String getRefSingleCafeCategoryDescription() {
        return refSingleCafeCategoryDescription;
    }

    public String getRefCafeCategoriesChild() {
        return refCafeCategoriesChild;
    }

    public String getRefCafeTablesChild() {
        return refCafeTablesChild;
    }

    public String getRefCafeCurrentOrders() {
        return getRefCafe() + refCafeCurrentOrders;
    }

    public String getRefCafeBills() {
        return getRefCafes() + menuViewModel.getCafeId().getValue() + refCafeBills;
    }

    public String getRefCafeTables() {
        return getRefCafe() + refCafeTables;
    }

    public String getRefCategoryDrinksChild() {
        return refCategoryDrinksChild;
    }

    /*countriesNumber*/


    /*drinksCategories*/
    public String getRefDrinksCategories() {
        return refDrinksCategories;
    }

    public String getRefDrinksCategory(String drinksCategoryId) {
        return getRefDrinksCategories() + drinksCategoryId;
    }

    public String getRefDrinksCategoriesChild(String drinksCategoryId) {
        return drinksCategoryId;
    }

    /*registeredNumbers*/
    public String getRefRegisteredNumbers() {
        return refRegisteredNumbers;
    }

    public String getRefRegisteredNumberWaiter() {
        return refRegisteredNumberWaiter;
    }

    public String getRefRegisteredNumberOwner() {
        return refRegisteredNumberOwner;
    }
    /*firebase storage*/

    public String getStorageCategoryDrinks() {
        return storageCategoryDrinks;
    }

    public String getStorageCategoryDrinksNoImage() {
        return storageCategoryDrinksNoImage;
    }

    public String getStorageCategories() {
        return storageCategories;
    }

    public String getStorageCountryFlags() {
        return storageCountryFlags;
    }

}
