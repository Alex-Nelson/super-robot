# super-robot
Based on iBuy app project from a Mobile App Development course I took in college. It is basically a list app.

The current version of the working app on the composeBranch implements the UI using the Jetpack Compose library.

## Working Features
- Create a list by clicking the Floating Action button and naming it.
- Delete lists by clicking the Options menu on the right side of the Top App Bar.
   - A list will be deleted if it has a checkmark next to it's name.

## Current Issues
Here is a list of current issues with the app:
- Currently, the swipe to dismiss feature (one way to delete a list on the Home Screen) does not work.
- The name of the current screen is not shown on the Top App Bar.

## Future Features
- Clicking a list will navigate a user to the next screen, to be called 'Manage List', where they can add items to the list.
   - They will be able to delete individual items as well as the current list being edited on this screen.
   - The user will also be able to rename the list on this screen.
