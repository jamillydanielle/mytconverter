import { Box } from '@mui/material';
export default function PrivateLayout({ children }: { children: React.ReactNode }) {
    return (
        <Box>
            <Box sx={{ backgroundColor: '#F0F0F0' }}>{children}</Box>
        </Box>
    );
}
