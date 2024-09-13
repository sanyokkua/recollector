import { Box, Button }                    from "@mui/material";
import { FC, useEffect }                  from "react";
import { Link }                           from "react-router-dom";
import { appBarSetCustomState }           from "../store/features/appBar/appBarSlice.ts";
import { useAppDispatch, useAppSelector } from "../store/hooks.ts";


const Welcome: FC = () => {
    const dispatch = useAppDispatch();
    const { userIsLoggedIn } = useAppSelector((state) => state.globals);

    useEffect(() => {
        dispatch(appBarSetCustomState("Welcome"));
    }, [dispatch]);

    return (
        <Box
            display="flex"
            flexDirection="column"
            alignItems="center"
            justifyContent="center"
            minHeight="100vh"
            p={ 2 }
            textAlign="left">
            <Box maxWidth="800px" width="100%">
                <h1>Welcome to Your Ultimate Organizer App!</h1>
                <p>
                    <strong>Discover a new way to keep track of your favorite movies, video games, and more!</strong>
                </p>
                <p>
                    Our app is designed to help you effortlessly manage and organize your entertainment and tasks.
                    Whether you're a movie buff, a gamer, or just someone who loves to stay organized, this app is
                    perfect for you.
                </p>

                <h2>Key Features:</h2>
                <ul>
                    <li><strong>Create Categories:</strong> Easily create custom categories to organize your items.
                        Whether it's movies, video games, or any other hobby, you can tailor the app to fit your
                        needs.
                    </li>
                    <li><strong>Add Items:</strong> Add items to each category with just a few clicks. Keep track of
                        what you've watched, played, or plan to enjoy in the future.
                    </li>
                    <li><strong>Status Tracking:</strong> Mark each item with a status like TODO_LATER, IN_PROGRESS,
                        or FINISHED. Always know where you stand with your entertainment and tasks.
                    </li>
                    <li><strong>Edit and Remove:</strong> Flexibly edit or remove categories and items as your
                        interests and priorities change.
                    </li>
                </ul>

                <h2>Use Cases:</h2>
                <ul>
                    <li><strong>Entertainment Tracker:</strong> Keep a detailed log of movies and video games you've
                        watched or played. Plan what to watch or play next and never lose track of your favorites.
                    </li>
                    <li><strong>To-Do List:</strong> Use the app as a versatile to-do list. Organize your tasks by
                        category, set their status, and stay on top of your daily activities.
                    </li>
                </ul>

                <h2>Why You'll Love It:</h2>
                <ul>
                    <li><strong>User-Friendly Interface:</strong> Intuitive design makes it easy for anyone to use.</li>
                    <li><strong>Customizable:</strong> Tailor the app to your specific needs and preferences.</li>
                    <li><strong>Stay Organized:</strong> Keep all your entertainment and tasks in one place, neatly
                        organized and easily accessible.
                    </li>
                </ul>

                <p>Start using our app today and transform the way you organize your life! 🎬🎮📝</p>
            </Box>
            { !userIsLoggedIn && <Box mt={ 2 } display="flex" justifyContent="center" gap={ 1 }>
                <Button component={ Link } to={ "/register" } variant="text">Register New Account</Button>
                <Button component={ Link } to={ "/login" } variant="contained" color="success">Login</Button>
            </Box> }
        </Box>
    );
};

export default Welcome;