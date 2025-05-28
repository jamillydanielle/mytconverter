"use client";

import React from "react";
import { Box, Typography } from "@mui/material";

interface IdentifierTitleProps {
  title: string;
  description?: string;
}

const IdentifierTitle: React.FC<IdentifierTitleProps> = ({ title, description }) => {
  return (
    <Box
      sx={{
        mb: 2,
        ml: 1
      }}
    >
      <Typography
        sx={{ color: "#000000", fontWeight: 700, fontSize: "24px"}}
      >
        {title}
      </Typography>
      {description && (
        <Typography variant="subtitle1" color="#555" sx={{ mt: 1 }}>
          {description}
        </Typography>
      )}
    </Box>
  );
};

export default IdentifierTitle;