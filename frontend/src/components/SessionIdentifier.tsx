"use client";

import React from "react";
import { Box, Tabs, Tab, styled } from "@mui/material";
import { useSessionIdentifier } from "@/hooks/useSessionIdentifier";

const StyledTab = styled(Tab)(({ theme }) => ({
  borderRadius: "4px",
  fontWeight: 700,
  fontSize: "16px",
  margin: "0 8px",
  minHeight: "auto",
  padding: "8px 12px",
  "&.Mui-selected": {
    backgroundColor: "#E1E6FF",
    color: "#485794",
  },
  "&:first-of-type": {
    marginLeft: 0,
  },
  textTransform : "none",
}));

const StyledTabs = styled(Tabs)({
  minHeight: "auto",
  "& .MuiTabs-indicator": {
    display: "none",
  },
});

export default function SessionIdentifier() {
  const { tabs, selectedTabIndex, handleTabChange } = useSessionIdentifier();

  return (
    <Box
      sx={{
        display: "flex",
        my: 7,
      }}
    >
      <StyledTabs
        value={selectedTabIndex}
        onChange={(_event, newValue) => handleTabChange(newValue)}
        aria-label="navigation tabs"
      >
        {tabs.map((tab, index) => (
          <StyledTab key={index} label={tab.label} />
        ))}
      </StyledTabs>
    </Box>
  );
}