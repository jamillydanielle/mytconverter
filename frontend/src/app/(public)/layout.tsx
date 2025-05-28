import { AlertProvider } from "@/components/alert/AlertProvider";

export default function PrivateLayout({ children }: { children: React.ReactNode }) {
    return (
        <div>
            <AlertProvider>
                <div>{children}</div>
            </AlertProvider>
        </div>
    );
}
