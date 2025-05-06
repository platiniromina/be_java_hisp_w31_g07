package com.mercadolibre.be_java_hisp_w31_g07.util;

import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class ErrorMessagesUtil {

    public static String buyerNotFound(UUID buyerId) {
        return "Buyer " + buyerId + " not found";
    }

    public static String sellerNotFound(UUID sellerId) {
        return "Seller " + sellerId + " not found";
    }

    public static String postNotFound(UUID postId) {
        return "Post " + postId + " not found";
    }

    public static String userNotFound(UUID userId) {
        return "User " + userId + " not found";
    }

    public static String invalidSortingParameter(String parameter) {
        return "Invalid sorting parameter: " + parameter;
    }

    public static String buyerIsNotFollowingAnySellers(UUID userId) {
        return "The buyer " + userId + " is not following any sellers";
    }

    public static String buyerNotFollowingSeller(UUID buyerId, UUID sellerId) {
        return "Buyer " + buyerId + " is not following seller " + sellerId;
    }

    public static String buyerAlreadyFollowingSeller(UUID buyerId, UUID sellerId) {
        return "Buyer " + buyerId + " is already following seller " + sellerId;
    }

    public static String buyerAndSellerAreTheSame(UUID buyerId, UUID sellerId) {
        return "Buyer " + buyerId + " and seller " + sellerId + " are the same";
    }

    public static String noPromotionPostFound(UUID userId) {
        return "No promotional posts found for user: " + userId;
    }

    public static String noPurchasesForProduct(String product) {
        return "No purchases found for product " + product;
    }

    public static String userHasNotPosts(UUID sellerId) {
        return "User " + sellerId + " has no posts.";
    }

    public static String noPromotionalPostUser(UUID userId) {
        return "No promotional posts found for user: " + userId;
    }

    public static String noValidParameter(String param) {
        return "Parameter '" + param + "' is invalid.";
    }
    
}