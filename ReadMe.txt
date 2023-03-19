First of all we need to run pre_run for once in life of database. 
User Interface:
Login : 3 type of logins a) Student b) Instructor c) Academic Office 
Based on the login they have 3 they will go into 3 different cases if they are logged in successfully.

For Student:
 0 - return graduation check of student.
 1 - view grades of that particular student.
 2 - cgpa of logged in student.
 3 - register for course for which offers are printed and to be selected and (year , sem) along with input.
 4 - deregister courses which are in 3rd option (slected when displayed).

For instructor:
 1 - view grades of any particular student.
 2 - register for new offering for which courses are printed and to be selected and additional requirements along with input.
 3 - deregister offeringswhich are in 3rd option (selected when displayed).
 4 - upload grades from a file where we need to enter a file path.(.csv required)

For acad_office:
 0 - to delete any course already in catalogue and to be deleted.
 1 - to add any course which is not in catalogue.
 2 - view grades of any particular student.
 3 - generate sgpa and cgpa and grades of student requested or parametered.
 4 - make permit closed for a particular offering after offering has been aired.



\gradlew build :
For percentage of code coverage, first load the database everytime we ran.
If we need to do it individually, remove pre_run() MainMethodTest which is made up of  some pre students, professors, departments and some courses
