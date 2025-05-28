interface ChangePasswordRequest {
  newPassword: string;
}

interface LoginUserResponse {
  message: string;
  token: string;
}

export const changeUserPassword = async (
  email: string,
  newPassword: string,
  token: string
): Promise<string> => {
  try {
    const response = await fetch(
      `http://localhost:3001/users/auth/change-password/${encodeURIComponent(
        email
      )}`,
      {
        method: "PUT",
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ newPassword } as ChangePasswordRequest),
      }
    );

    if (!response.ok) {
      const errorMessage = await response.text();
      throw new Error(errorMessage);
    }

    const data = await response.text();
    return data;
  } catch (error) {
    if (error instanceof Error) {
      throw error;
    }
    throw new Error("An unknown error occurred");
  }
};

export const loginUser = async (
  username: string,
  password: string,
  rememberMe: boolean
): Promise<LoginUserResponse> => {
  try {
    //Essa rota ainda nao esta criada no backend
    const response = await fetch("http://localhost:3001/users/auth/login", {
      headers: { "Content-Type": "application/json" },
      method: "POST",
      body: JSON.stringify({ email: username, password, rememberMe }),
    });

    const data = await response.json();

    if (!response.ok) {
      if (
        response.status === 403 &&
        data.message === "Password needs to be changed"
      ) {
        return { token: data.token || "", message: data.message };
      }
      throw new Error(data.message);
    }

    return { token: data.token || "", message: data.message };
  } catch (error) {
    if (error instanceof Error) {
      throw error;
    }
    throw new Error("An unknown error occurred");
  }
};