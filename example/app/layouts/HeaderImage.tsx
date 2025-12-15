import { Image } from 'expo-image';
import type { HeaderImageProps } from './types';
import { StyleSheet } from 'react-native';
import { IconSymbol } from '@/components/ui/icon-symbol';

const HeaderImage = ({ image }: HeaderImageProps) => {
  if (image === 'reactLogo') {
    return (
      <Image
        source={require('@/assets/images/partial-react-logo.png')}
        style={styles.reactLogo}
      />
    );
  }

  return (
    <IconSymbol
      color={image.color}
      name={image.name}
      size={310}
      style={styles.headerImage}
    />
  );
};

export default HeaderImage;

const styles = StyleSheet.create({
  headerImage: {
    bottom: -90,
    left: -35,
    position: 'absolute',
  },
  reactLogo: {
    height: 178,
    width: 290,
    bottom: 0,
    left: 0,
    position: 'absolute',
  },
});
