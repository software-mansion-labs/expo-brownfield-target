import ParallaxScrollView from '@/components/parallax-scroll-view';
import { RootLayoutProps } from './types';
import { ThemedView } from '@/components/themed-view';
import { StyleSheet } from 'react-native';
import { ThemedText } from '@/components/themed-text';
import { HelloWave } from '@/components/hello-wave';
import HeaderImage from './HeaderImage';
import { Fonts } from '@/constants/theme';

const RootLayout = ({ children, headerOptions }: RootLayoutProps) => {
  const { showWave, title, headerImage, ...rest } = headerOptions;

  return (
    <ParallaxScrollView
      headerImage={<HeaderImage image={headerImage} />}
      {...rest}
    >
      <ThemedView style={styles.titleContainer}>
        <ThemedText
          style={{
            fontFamily: Fonts.rounded,
          }}
          type="title"
        >
          {title}
        </ThemedText>
        {showWave && <HelloWave />}
      </ThemedView>
      {children}
    </ParallaxScrollView>
  );
};

export default RootLayout;

const styles = StyleSheet.create({
  titleContainer: {
    flexDirection: 'row',
    alignItems: 'center',
    gap: 8,
  },
});
