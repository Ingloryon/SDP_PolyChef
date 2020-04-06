package ch.epfl.polychef;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.intent.rule.IntentsTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.polychef.fragments.OnlineMiniaturesFragment;
import ch.epfl.polychef.image.ImageStorage;
import ch.epfl.polychef.pages.EntryPage;
import ch.epfl.polychef.recipe.Ingredient;
import ch.epfl.polychef.recipe.Recipe;
import ch.epfl.polychef.recipe.RecipeBuilder;
import ch.epfl.polychef.recipe.RecipeStorage;
import ch.epfl.polychef.utils.RecipeMiniatureAdapter;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.RootMatchers.withDecorView;
import static androidx.test.espresso.matcher.ViewMatchers.hasSibling;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

public class RecipeMiniatureImageTest {

    @Rule
    public IntentsTestRule<EntryPage> intentsTestRule = new IntentsTestRule<>(EntryPage.class);

    @Mock
    RecipeStorage recipeStorage;

    private RecipeBuilder recipeBuilder = new RecipeBuilder()
            .addInstruction("test instruction")
            .setPersonNumber(4)
            .setEstimatedCookingTime(35)
            .setEstimatedPreparationTime(40)
            .addIngredient("test", 1.0, Ingredient.Unit.CUP)
            .setMiniatureFromPath("test_path")
            .setRecipeDifficulty(Recipe.Difficulty.EASY);

    private Recipe recipe1 = recipeBuilder.setName("test1") .build();

    private Recipe recipe2 = recipeBuilder.setName("test2") .build();

    private RecipeMiniatureImageTest.FragmentTest fragment;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        doAnswer((call) -> {
            CallNotifier<Recipe> ch = call.getArgument(2);
            ch.notify(recipe1);
            ch.notify(recipe2);
            return null;
        }).when(recipeStorage).getNRecipesOneByOne(any(Integer.class), any(Integer.class), any(CallNotifier.class));
    }

    private void setUp() {
        Bundle bundle = new Bundle();
        bundle.putSerializable("RecipeStorage", recipeStorage);
        bundle.putInt("fragmentID", R.id.nav_entry_fragment);
        fragment = new RecipeMiniatureImageTest.FragmentTest();
        fragment.setArguments(bundle);
        FragmentTransaction transaction = intentsTestRule.getActivity().getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.nav_entry_fragment, fragment).addToBackStack(null);
        transaction.commit();
    }

    @Test
    public synchronized void canShowOnlineMiniature() throws InterruptedException {
        setUp();
        wait(1000);
        assertEquals(2, fragment.getRecyclerView().getAdapter().getItemCount());
        onView(allOf(withId(R.id.miniatureRecipeImage), hasSibling(withText("test1")))).check(matches(isDisplayed()));
        onView(allOf(withId(R.id.miniatureRecipeImage), hasSibling(withText("test2")))).check(matches(not(isDisplayed())));
    }

    public static class FragmentTest extends OnlineMiniaturesFragment {
        private RecyclerView onlineRecyclerView;
        private RecipeStorage recipeStorage;
        private int currentReadInt = 1;
        private List<Recipe> dynamicRecipeList = new ArrayList<>();

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.fragment_miniatures_online, container, false);

            Bundle bundle = getArguments();
            recipeStorage = (RecipeStorage) bundle.getSerializable("RecipeStorage");

            int fragmentID = bundle.getInt("fragmentID");
            onlineRecyclerView = view.findViewById(R.id.miniaturesOnlineList);
            onlineRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
            onlineRecyclerView.setAdapter(new FakeRecipeMiniatureAdapter(this.getActivity(), dynamicRecipeList, onlineRecyclerView, fragmentID));
            return view;
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            initFirstNRecipes();
        }

        private void initFirstNRecipes() {
            dynamicRecipeList.clear();
            recipeStorage.getNRecipesOneByOne(nbOfRecipesLoadedAtATime, 1, this);
            currentReadInt += nbOfRecipesLoadedAtATime;
        }

        @Override
        public void notify(Recipe data) {
            dynamicRecipeList.add(data);
            onlineRecyclerView.getAdapter().notifyDataSetChanged();
        }

        @Override
        public void onSuccess(List<Recipe> data) {
            dynamicRecipeList.addAll(data);
            onlineRecyclerView.getAdapter().notifyDataSetChanged();
        }

        @Override
        public void onFailure() {
        }

        public RecyclerView getRecyclerView(){
            return onlineRecyclerView;
        }
    }

    public static class FakeRecipeMiniatureAdapter extends RecipeMiniatureAdapter {
        public FakeRecipeMiniatureAdapter(Context mainContext, List<Recipe> recipeList, RecyclerView recyclerView, int fragmentContainerID) {
            super(mainContext, recipeList, recyclerView, fragmentContainerID);
        }

        @Override
        public ImageStorage getImageStorage() {
            return new FakeImageStorage();
        }
    }

    private static class FakeImageStorage extends ImageStorage {
        @Override
        public void getImage(String imageName, CallHandler<byte[]> caller) {
            byte[] data = new byte[] {1, 2, 3, 4, 3, 2, 1};
            if(imageName.equals("test_path")) {
                caller.onSuccess(data);
            } else {
                caller.onFailure();
            }
        }
    }
}
