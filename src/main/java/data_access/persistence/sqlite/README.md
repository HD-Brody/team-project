# Accessing Data
2025 Nov 10 by Leo Wang

To start, create a new Connection using the get_connection method of the SQLiteConnectionFactory class.
You'll need this Connection instance to call methods defined within this folder.

To work with the upload syllabus functionality and access its stored data, go to the UploadSyllabus class and initialize an instance and have the Connection instance that you just created as the parameter. 
You can then interact with its methods to save, edit, or delete any information as required. 
The same steps apply to other members.