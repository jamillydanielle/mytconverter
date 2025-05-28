"use client";

import React from 'react';
import { useRouter } from 'next/navigation';
import { TextField, Button, Container, Typography, Box, Checkbox, FormControlLabel } from '@mui/material';
import { useLoginForm } from '@/hooks/useLoginForm';

function LoginPage() {
    const { handleChange, handleLogin, credentials, error, password, confirmPassword, loginSuccess, changePassword } = useLoginForm();
    const router = useRouter();

    React.useEffect(() => {
        if (loginSuccess) {
            router.push('/');
        }
    }, [loginSuccess, router]);

    return (
        <Container maxWidth="sm">
            <Box sx={{ mt: 8, display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                <Typography component="h1" variant="h5">
                    Login
                </Typography>
                <Box component="form" onSubmit={(e) => { e.preventDefault(); handleLogin(); }} noValidate sx={{ mt: 1, width: '100%' }}>
                    <TextField
                        margin="normal"
                        required
                        fullWidth
                        id="email"
                        label="Email Address"
                        name="email"
                        autoComplete="email"
                        autoFocus
                        value={credentials.email}
                        onChange={handleChange}
                    />
                    {changePassword ? (
                         <>
                         <TextField
                             margin="normal"
                             required
                             fullWidth
                             name="newPassword"
                             label="New Password"
                             type="password"
                             id="newPassword"
                             autoComplete="new-password"
                             value={confirmPassword}
                             onChange={handleChange}
                         />
                          <TextField
                             margin="normal"
                             required
                             fullWidth
                             name="currentPassword"
                             label="Current Password"
                             type="password"
                             id="currentPassword"
                             autoComplete="current-password"
                             value={password}
                             onChange={handleChange}
                         />
                         </>
                    ) : (
                        <TextField
                            margin="normal"
                            required
                            fullWidth
                            name="password"
                            label="Password"
                            type="password"
                            id="password"
                            autoComplete="current-password"
                            value={credentials.password}
                            onChange={handleChange}
                        />
                    )}
                    <FormControlLabel
                        control={<Checkbox value={credentials.rememberMe} name="rememberMe" color="primary" onChange={handleChange} />}
                        label="Remember me"
                    />
                    {error && (
                        <Typography color="error" sx={{ mt: 1 }}>
                            {error}
                        </Typography>
                    )}
                    <Button
                        type="submit"
                        fullWidth
                        variant="contained"
                        sx={{ mt: 3, mb: 2 }}
                    >
                        Login
                    </Button>
                </Box>
            </Box>
        </Container>
    );
}

export default LoginPage;