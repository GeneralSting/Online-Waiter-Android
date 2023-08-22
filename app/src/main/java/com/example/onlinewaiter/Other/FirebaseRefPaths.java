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
    //paths
    private final String refCafes = "cafes/";
    private final String refCafeName = "/cafeName";
    private final String refcafeLocation = "/cafeLocation";
    private final String refCafeOwnerEmail = "/cafeOwnerGmail";
    private final String refCafeCategories = "/cafeDrinksCategories/";
    private final String refCategoryDrinks = "/categoryDrinks/";
    private final String refCafeBills = "/cafeBills/";
    private final String refCafeCurrentOrders = "/cafeCurrentOrders/";
    private final String refCafeCategoryName = "/cafeCategoryName/";
    private final String refCafeCategoryDescription = "/cafeCategoryDescription/";
    private final String refCafeCategoryImage = "/cafeCategoryImage/";
    private final String refCategoryDrinkImage = "/categoryDrinkImage";
    private final String refCafeTables = "/cafeTables/";
    private final String refCurrentOrderDrinks = "/currentOrderDrinks/";
    private final String refCafeCountry = "/cafeCountry";
    private final String refCategoryDrinkQuantity = "/categoryDrinkQuantity";

    //child
    private final String refCategoryDrinkNameChild = "categoryDrinkName";
    private final String refCafeCategoryDescriptionChild = "cafeCategoryDescription";
    private final String refCafeCategoryImageChild = "cafeCategoryImage";
    private final String refCafeCategoryNameChild = "cafeCategoryName";
    private final String refCafeBillsChild = "cafeBills";
    private final String refCafeCurrentOrdersChild = "cafeCurrentOrders";
    private final String refCafeCategoriesChild = "cafeDrinksCategories";
    private final String refCafeTablesChild = "cafeTables";
    private final String refCategoryDrinksChild = "categoryDrinks";
    private final String refCurrentOrderDelivererChild = "currentOrderDelivererEmployee";
    private final String refCurrentOrderStatusChild = "currentOrderStatus";
    private final String refCurrentOrderMessageChild = "currentOrderMessage";
    private final String refCafeNameChild = "cafeName";
    private final String refCurrentOrderTableNumberChild = "currentOrderTableNumber";
    private final String refCafeCountryChild = "cafeCountry";

    /*countriesNumber*/


    /*drinksCategories*/
    private final String refDrinksCategories = "drinksCategories/";


    /* registeredCountries */
    private final String refRegisteredCountries = "/registeredCountries/";
    private final String refRCCurrency = "/currency";
    private final String refRCDateTimeFormat = "/dateTimeFormat";
    private final String refRCDecimalSeperator = "/decimalSeperator";


    /*registeredNumbers*/
    //paths
    private final String refRegisteredNumbers = "registeredNumbers/";
    private final String refRegisteredNumberAllowed = "/allowed/";

    //child
    private final String refRegisteredNumberRoleChild = "role";
    private final String refRegisteredNumberAllowedChild = "allowed";
    private final String refRegisteredNumberWebChild = "webAppRegistered";
    private final String refRNMemoryWordChild = "memoryWord";

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
        menuViewModel = new ViewModelProvider(viewModelStoreOwner).get(MenuViewModel.class);
    }

    /*Methods*/
    /*appError*/
    public String getAppErrors() {
        return refAppErrors;
    }

    /*appInfo*/
    public String getAppInfo() {
        return refAppInfo;
    }

    /*cafes*/
    public String getCafes() {
        return refCafes;
    }

    public String getCafe() {
        return getCafes() + menuViewModel.getCafeId().getValue();
    }

    public String getCafeOwner(String cafeId) {
        return getCafes() + cafeId;
    }

    public String getCafeCountryOwner(String cafeId) {
        return getCafeOwner(cafeId) + refCafeCountry;
    }
    public String getCafeBillsOwner(String cafeId) {
        return getCafeOwner(cafeId) + refCafeBills;
    }

    public String getCafeNameChild() {
        return refCafeNameChild;
    }

    public String getCafeCategories() {
        return getCafe() + refCafeCategories;
    }

    public String getCafeCategory(String cafeCategoryId) {
        return getCafeCategories() + cafeCategoryId;
    }

    public String getCafeCategoryName(String cafeCategoryId) {
        return getCafeCategory(cafeCategoryId) + refCafeCategoryName;
    }

    public String getCategoryDrinks(String cafeCategoryId) {
        return getCafeCategory(cafeCategoryId) + refCategoryDrinks;
    }

    public String getCategoryDrink(String cafeCategoryId, String categoryDrinkId) {
        return getCategoryDrinks(cafeCategoryId) + categoryDrinkId;
    }

    public String getCategoryDrinkNameChild() {
        return refCategoryDrinkNameChild;
    }

    public String getCategoryDrinkQuantity(String cafeCategoryId, String categoryDrinkId) {
        return getCategoryDrink(cafeCategoryId, categoryDrinkId) + refCategoryDrinkQuantity;
    }

    public String getCategoryDrinkImage(String cafeCategoryId, String categoryDrinkId) {
        return getCategoryDrink(cafeCategoryId, categoryDrinkId) + refCategoryDrinkImage;
    }

    public String getCafeCategoryNameChild() {
        return refCafeCategoryNameChild;
    }

    public String getCafeCategoryImageChild() {
        return refCafeCategoryImageChild;
    }

    public String getCafeCategoriesChild() {
        return refCafeCategoriesChild;
    }

    public String getCafeTablesChild() {
        return refCafeTablesChild;
    }

    public String getCafeCurrentOrders() {
        return getCafe() + refCafeCurrentOrders;
    }

    public String getCafeCurrentOrder(String currentOrderId) {
        return getCafeCurrentOrders() + currentOrderId;
    }

    public String getCurrentOrderDrinks(String currentOrderId) {
        return getCafeCurrentOrder(currentOrderId) + refCurrentOrderDrinks;
    }

    public String getCurrentOrderDelivererChild() {
        return refCurrentOrderDelivererChild;
    }

    public String getCurrentOrderStatusChild() {
        return refCurrentOrderStatusChild;
    }

    public String getCafeBills() {
        return getCafe() + refCafeBills;
    }

    public String getCafeTables() {
        return getCafe() + refCafeTables;
    }

    public String getCategoryDrinksChild() {
        return refCategoryDrinksChild;
    }

    public String getCafeCurrentOrdersChild() {
        return refCafeCurrentOrdersChild;
    }

    public String getCafeBillsChild() {
        return refCafeBillsChild;
    }


    public String getCurrentOrderTableNumberChild() {
        return refCurrentOrderTableNumberChild;
    }

    public String getCafeCountryChild() {
        return refCafeCountryChild;
    }

    public String getCafeCountry() {
        return getCafe() + refCafeCountry;
    }

    public String getCafeNameOwner(String cafeId) {
        return getCafeOwner(cafeId) + refCafeName;
    }

    public String getCafeLocationOwner(String cafeId) {
        return getCafeOwner(cafeId) + refcafeLocation;
    }

    public String getCafeOwnerEmail(String cafeId) {
        return getCafeOwner(cafeId) + refCafeOwnerEmail;
    }

    /*countriesNumber*/


    /*drinksCategories*/
    public String getDrinksCategories() {
        return refDrinksCategories;
    }

    public String getDrinksCategory(String drinksCategoryId) {
        return getDrinksCategories() + drinksCategoryId;
    }

    public String getDrinksCategoriesChild(String drinksCategoryId) {
        return drinksCategoryId;
    }


    /* registeredCountries */
    public String getRegisteredCountries() {
        return refRegisteredCountries;
    }

    public String getRegisteredCountry(String registeredCountry) {
        return getRegisteredCountries() + registeredCountry;
    }

    /*registeredNumbers*/
    public String getRegisteredNumbers() {
        return refRegisteredNumbers;
    }

    public String getCafeRegisteredNumbers(String registeredCafeId) {
        return getRegisteredNumbers() + registeredCafeId;
    }

    public String getRegisteredNumber(String registeredCafeId, String registeredNumberId) {
        return getCafeRegisteredNumbers(registeredCafeId) + AppConstValue.characterConstValue.FIREBASE_SLASH + registeredNumberId;
    }

    public String getRegisteredNumberAllowed(String registeredCafeId, String registeredNumberId) {
        return getRegisteredNumber(registeredCafeId, registeredNumberId) + refRegisteredNumberAllowed;
    }

    public String getRegisteredNumberChild(String registeredNumberChild) {
        return registeredNumberChild;
    }

    public String getRegisteredNumberRoleChild() {
        return refRegisteredNumberRoleChild;
    }

    public String getRegisteredNumberAllowedChild() {
        return refRegisteredNumberAllowedChild;
    }

    public String getRegisteredNumberWebChild() {
        return refRegisteredNumberWebChild;
    }

    public String getRNMemoryWordChild() {
        return refRNMemoryWordChild;
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
