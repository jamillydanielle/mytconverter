import React from "react";
import { Modal, Box, Typography } from "@mui/material";
import GenericButton from "./GenericButton"; // Certifique-se de ajustar o caminho da importação conforme necessário

type ModalProps = {
  title: string;
  supportingText: string;
  primaryButtonLabel?: string;
  closeButtonLabel: string;
  onPrimaryAction?: () => void;
  onCloseAction: () => void;
};

const CustomModal: React.FC<ModalProps> = ({
  title,
  supportingText,
  primaryButtonLabel,
  closeButtonLabel,
  onPrimaryAction = () => {},
  onCloseAction,
}) => {
  return (
    <Modal
      open={true} // Sempre aberto, pois o controle é feito no pai
      onClose={onCloseAction}
      className="flex items-center justify-center"
    >
      <Box
        className="bg-white p-6 rounded-xl shadow-lg"
        sx={{ width: 500, height: "auto", outline: "none" }}
      >
        <Box sx={{ mb: 1}}>
          <Typography
            variant="h6"
            className="font-bold text-black"
            sx={{ fontSize: 20, mt: 3, ml: 3, fontWeight: 700 }}
          >
            {title}
          </Typography>
          <Typography
            variant="body2"
            className="text-black"
            sx={{ fontSize: 16, mt: 2, ml: 3, fontWeight: 400 }}
          >
            {supportingText}
          </Typography>
        </Box>
        <Box
          sx={{
            display: "flex",
            justifyContent: "flex-end",
            mr: "20px",
            mb: "20px",
            gap: "16px",
          }}
        >
          <GenericButton
            text={closeButtonLabel}
            onClick={onCloseAction}
            sx={{
              width: "auto",
              fontSize: 16,
              fontWeight: 700,
              textTransform: "none",
              backgroundColor: "transparent", // Sem fundo
              color: "black", // Apenas texto em preto
              boxShadow: "none", // Remove qualquer sombra
              "&:hover": {
                backgroundColor: "transparent", // Sem fundo no hover também
              },
            }}
          />
          {primaryButtonLabel && (
            <GenericButton
              text={primaryButtonLabel}
              onClick={onPrimaryAction}
              sx={{
                width: "auto",
                fontSize: 16,
                fontWeight: 700,
                backgroundColor: "#A34A87",
                color: "white",
                textTransform: "none",
                "&:hover": {
                  backgroundColor: "#943e6d",
                },
              }}
            />
          )}
        </Box>
      </Box>
    </Modal>
  );
};

export default CustomModal;
