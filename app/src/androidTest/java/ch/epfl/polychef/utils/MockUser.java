package ch.epfl.polychef.utils;

import android.net.Uri;
import android.os.Parcel;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.internal.firebase_auth.zzff;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.zzy;
import com.google.firebase.auth.zzz;

import java.util.ArrayList;
import java.util.List;

public class MockUser {

    public static FirebaseUser getMockUser() {
        {
            return new FirebaseUser() {
                @NonNull
                @Override
                public String getUid() {
                    return "1234";
                }

                @NonNull
                @Override
                public String getProviderId() {
                    return "PolyChef";
                }

                @Override
                public boolean isAnonymous() {
                    return false;
                }

                @Nullable
                @Override
                public List<String> zza() {
                    return null;
                }

                @NonNull
                @Override
                public List<? extends UserInfo> getProviderData() {
                    return new ArrayList<>();
                }

                @NonNull
                @Override
                public FirebaseUser zza(@NonNull List<? extends UserInfo> list) {
                    return this;
                }

                @Override
                public FirebaseUser zzb() {
                    return null;
                }

                @NonNull
                @Override
                public FirebaseApp zzc() {
                    return FirebaseApp.getInstance();
                }

                @Nullable
                @Override
                public String getDisplayName() {
                    return "Mock User";
                }

                @Nullable
                @Override
                public Uri getPhotoUrl() {
                    return null;
                }

                @Nullable
                @Override
                public String getEmail() {
                    return "mock@test.polychef";
                }

                @Nullable
                @Override
                public String getPhoneNumber() {
                    return null;
                }

                @Nullable
                @Override
                public String zzd() {
                    return null;
                }

                @NonNull
                @Override
                public zzff zze() {
                    return new zzff();
                }

                @Override
                public void zza(@NonNull zzff zzff) {

                }

                @NonNull
                @Override
                public String zzf() {
                    return "string";
                }

                @NonNull
                @Override
                public String zzg() {
                    return "string";
                }

                @Nullable
                @Override
                public FirebaseUserMetadata getMetadata() {
                    return null;
                }

                @NonNull
                @Override
                public zzz zzh() {
                    return new zzz() {
                        @NonNull
                        @Override
                        public List<zzy> zza() {
                            return new ArrayList<>();
                        }
                    };
                }

                @Override
                public void zzb(List<zzy> list) {

                }

                @Override
                public void writeToParcel(Parcel dest, int flags) {

                }

                @Override
                public boolean isEmailVerified() {
                    return false;
                }
            };
        }
    }
}
