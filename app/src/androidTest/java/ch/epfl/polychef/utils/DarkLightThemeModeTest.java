package ch.epfl.polychef.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.intercepting.SingleActivityFactory;

import org.junit.Rule;
import org.junit.Test;

import ch.epfl.polychef.GlobalApplication;
import ch.epfl.polychef.pages.EntryPage;

import static androidx.test.espresso.matcher.ViewMatchers.assertThat;
import static org.hamcrest.core.Is.is;

public class DarkLightThemeModeTest {

    private SingleActivityFactory<EntryPage> fakeEntryPage = new SingleActivityFactory<EntryPage>(
            EntryPage.class) {
        @Override
        protected EntryPage create(Intent intent) {
            EntryPage activity = new FakeEntryPage();
            return activity;
        }
    };

    @Rule
    public ActivityTestRule<EntryPage> intentsTestRule = new ActivityTestRule<>(fakeEntryPage, false, false);

    @Test
    public void canStartInDarkMode() {
        removePref();
        setSharedPref(true);
        intentsTestRule.launchActivity(new Intent());
        assertThat(AppCompatDelegate.getDefaultNightMode(), is(AppCompatDelegate.MODE_NIGHT_YES));
    }

    @Test
    public void startInLightModeByDefault() {
        removePref();
        intentsTestRule.launchActivity(new Intent());
        assertThat(AppCompatDelegate.getDefaultNightMode(), is(AppCompatDelegate.MODE_NIGHT_NO));
    }

    @Test
    public void canStartInLightMode() {
        removePref();
        setSharedPref(false);
        intentsTestRule.launchActivity(new Intent());
        assertThat(AppCompatDelegate.getDefaultNightMode(), is(AppCompatDelegate.MODE_NIGHT_NO));
    }

    private void removePref() {
        SharedPreferences sharedPreferences = GlobalApplication.getAppContext().getSharedPreferences("darkMode", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("darkMode");
        editor.apply();
    }

    public static void setSharedPref(boolean value) {
        SharedPreferences sharedPreferences = GlobalApplication.getAppContext().getSharedPreferences("darkMode", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("darkMode", value ? "true" : "false");
        editor.apply();
    }

    public static class FakeEntryPage extends EntryPage {
        @Override
        protected void goHomeIfConnected() {
        }

        @Override
        protected int getUIMode() {
            return 17;
        }
    }
}
