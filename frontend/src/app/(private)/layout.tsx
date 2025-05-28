import { AppRouterCacheProvider } from '@mui/material-nextjs/v15-appRouter';
import { ThemeProvider } from '@mui/material/styles';
import { DM_Sans } from "next/font/google";
import theme from '@/theme';
import '@/global.css';
import { AlertProvider } from '@/components/alert/AlertProvider';
import Header from '@/components/header/header';
import SessionIdentifier from '@/components/SessionIdentifier';
import { Box } from '@mui/material';

const dmSans = DM_Sans({ subsets: ["latin"], weight: ["400", "500", "700"], variable: "--font-dm-sans" });

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="pt-BR">
      <body className={dmSans.className}>
        <AppRouterCacheProvider>
          <AlertProvider>
            <ThemeProvider theme={theme}>
              <Header />
              <Box sx={{ display: "flex", flexDirection: "column", justifyContent: "center", alignItems: "center", pb: 15 }}>
                <Box sx={{ width: "80vw" }}>
                  <SessionIdentifier />
                  {children}
                </Box>
              </Box>
            </ThemeProvider>
          </AlertProvider>
        </AppRouterCacheProvider>
      </body>
    </html>
  );
}