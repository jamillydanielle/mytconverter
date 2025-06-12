import React from 'react';

interface FeatureCardProps {
  icon: React.ComponentType; 
  title: string;
  onClick: () => void;
}

const FeatureCard: React.FC<FeatureCardProps> = ({ icon: IconComponent, title, onClick }) => {  
  return (
    <div onClick={onClick}>
      <IconComponent /> 
      <h3>{title}</h3>
    </div>
  );
};

export default FeatureCard;