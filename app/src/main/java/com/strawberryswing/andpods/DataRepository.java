package com.strawberryswing.andpods;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.strawberryswing.andpods.db.entity.CommentEntity;
import com.strawberryswing.andpods.db.entity.ProductEntity;
import com.strawberryswing.andpods.db.AppDatabase;

import java.util.List;

/**
 * Repository handling the work with products and comments.
 */
public class DataRepository {

    private static com.strawberryswing.andpods.DataRepository sInstance;

    private final AppDatabase mDatabase;
    private MediatorLiveData<List<ProductEntity>> mObservableProducts;

    private DataRepository(final AppDatabase database) {
        mDatabase = database;
        mObservableProducts = new MediatorLiveData<>();

        mObservableProducts.addSource(mDatabase.productDao().loadAllProducts(),
                productEntities -> {
                    if (mDatabase.getDatabaseCreated().getValue() != null) {
                        mObservableProducts.postValue(productEntities);
                    }
                });
    }

    public static com.strawberryswing.andpods.DataRepository getInstance(final AppDatabase database) {
        if (sInstance == null) {
            synchronized (com.strawberryswing.andpods.DataRepository.class) {
                if (sInstance == null) {
                    sInstance = new com.strawberryswing.andpods.DataRepository(database);
                }
            }
        }
        return sInstance;
    }

    /**
     * Get the list of products from the database and get notified when the data changes.
     */
    public LiveData<List<ProductEntity>> getProducts() {
        return mObservableProducts;
    }

    public LiveData<ProductEntity> loadProduct(final int productId) {
        return mDatabase.productDao().loadProduct(productId);
    }

    public LiveData<List<CommentEntity>> loadComments(final int productId) {
        return mDatabase.commentDao().loadComments(productId);
    }
}
