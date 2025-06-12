import React from 'react';

interface FeatureCardProps {
  icon: React.ComponentType; // OR React.ReactNode if you want to allow JSX or strings
  title: string;
  onClick: () => void;
}

const FeatureCard: React.FC<FeatureCardProps> = ({ icon: IconComponent, title, onClick }) => {  //Renamed to IconComponent
  return (
    <div onClick={onClick}>
      {/* Render the IconComponent directly.  Must be capitalized! */}
      <IconComponent /> {/* You might want to control the size */}
      <h3>{title}</h3>
    </div>
  );
};

export default FeatureCard;