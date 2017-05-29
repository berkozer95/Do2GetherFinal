# Fingo
 
This is an android application.

There are some requirements for running the application.

Facebook App ID, 

Google API KEY,

Android Studio, Gradle 2.2.3,

Android Emulator(min SDK is 15)

Jupyter Notebook for running ipynb,



In this android application, users are able to create accounts and manage it as they wish. 
Users can select a place from and Place Picker UI widget. After selecting a place from the UI Widget, there will 
be details for the selected place (name, rating, price level, open now?, url, phone number, reviews). Also, there are 2 more options.
One of them is getting directions to the selected place. In get Directions option, there are 3 tabs which are Driving, Walking and 
Transit. User can select from them and see the instructions. Other option is "Who Else Is Here" option. When it is selected, the other
users who are in that selected place are seen. The users are listed depending on how their hobbies match current users hobbies. When a
user is selected from list, selected user's profile can be reviewed and there is chat option for chat with selected user.

Attention!

To be able to have benefits of user matching, you need to run your android device while it is connected to the pc. After you run the application on the device and face the users listing page, you should open platfoorm-tools and run adb.exe. In the console write "adb pull sdcard/Hobbies /'your_directory'" and later run the Word2Vec.ipyhb. After you run Word2Vec.ipynb, you write "adb push /'your_directory' sdcard/" to your console again. After this instructions, when you refresh the device screen, users will be listed depending on their hobby matching similarities.
