import {useRouteError} from "react-router-dom";
import {FC} from "react";

const ErrorPage: FC = () => {
    const error = useRouteError();
    const errorAsObj: { statusText?: string, message?: string } = error as { statusText?: string, message?: string };

    return <>
        <h1>Oops!</h1>
        <p>Sorry, an unexpected error has occurred.</p>
        <p>
            <i>{errorAsObj?.statusText ?? errorAsObj?.message}</i>
        </p>
    </>;
};

export default ErrorPage;