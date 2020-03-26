package ch.epfl.polychef;

import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.*;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.fragment.NavHostFragment;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.List;

import ch.epfl.polychef.recipe.OfflineRecipes;
import ch.epfl.polychef.recipe.RecipeStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OnlineMiniaturesFragmentTest {

    private RecipeStorage fakeRecipeStorage = new FakeRecipeStorage();
    private FirebaseDatabase firebaseInstance;

    private SingleActivityFactory<HomePage> fakeHomePage = new SingleActivityFactory<HomePage>(
            HomePage.class) {
        @Override
        protected HomePage create(Intent intent) {
            HomePage activity = new FakeHomePage();
            return activity;
        }
    };

    @Rule
    public ActivityTestRule<HomePage> intentsTestRule = new ActivityTestRule<>(fakeHomePage, false,
            true);

    public void initActivity() {
        intentsTestRule.launchActivity(new Intent());
    }



    @After
    public void finishActivity(){
        intentsTestRule.finishActivity();
    }

    @Before
    public void initMockTest() {
        firebaseInstance = mock(FirebaseDatabase.class);
        DatabaseReference fakeDatabaseReference = mock(DatabaseReference.class);
        when(fakeDatabaseReference.child(anyString())).thenReturn(fakeDatabaseReference);
        when(firebaseInstance.getReference(anyString())).thenReturn(fakeDatabaseReference);
    }

    @Test
    public synchronized void databaseEmptyAddNothingToView() throws InterruptedException {
        initActivity();
        assertEquals(0, getMiniaturesFragment().getRecyclerView().getAdapter().getItemCount());
    }

    @Test
    public synchronized void databaseOneElementIsDisplayedOnActivityLoad(){
        fakeRecipeStorage.addRecipe(OfflineRecipes.getInstance().getOfflineRecipes().get(0));
        initActivity();
        // Incoming code 

    }

    public OnlineMiniaturesFragment getMiniaturesFragment(){
        FragmentManager fragmentManager = intentsTestRule.getActivity().getSupportFragmentManager();

        NavHostFragment hostFragment = (NavHostFragment)
                fragmentManager.findFragmentById(R.id.nav_host_fragment);

        return (OnlineMiniaturesFragment) hostFragment.getChildFragmentManager().getFragments().get(0);
    }




    private class FakeHomePage extends HomePage {

        @Override
        public FirebaseUser getUser() {
            return Mockito.mock(FirebaseUser.class);
        }


    }

    public class FakeRecipeStorage extends RecipeStorage {
        @Override
        public FirebaseDatabase getFirebaseDatabase() {
            return firebaseInstance;
        }
    }



}
