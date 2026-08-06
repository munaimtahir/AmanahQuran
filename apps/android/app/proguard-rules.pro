# Release shrinker rules for Amanah Quran.

# Room Database and DAO generated implementations keep rules
-keep class * extends androidx.room.RoomDatabase {
    public <init>();
}
-keep class * extends androidx.room.RoomDatabase_Impl {
    public <init>();
}
-keep class **.*_Impl {
    public <init>();
    *;
}
-keep @androidx.room.Dao interface * { *; }
-keep @androidx.room.Database class * { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public <init>();
}
