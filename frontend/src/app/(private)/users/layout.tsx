import React from "react";
import IdentifierTitle from "@/components/IdentifierTitle";
import { Box } from "@mui/material";

export default function PrivateLayout({ children }: { children: React.ReactNode }) {
  return (
    <Box>
        <Box>
          <IdentifierTitle title="Admin" />
          {children}
        </Box>
    </Box>
  );
}