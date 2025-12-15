import { useEffect } from 'react';
import ExpoBrownfield from 'expo-brownfield-target';
import { useNavigation } from 'expo-router';

const useBackHandling = () => {
  const navigation = useNavigation();

  useEffect(() => {
    const unsubscribe = navigation.addListener('state', () => {
      const shouldEnableNativeBack = navigation.canGoBack();
      ExpoBrownfield.setNativeBackEnabled(!shouldEnableNativeBack);
    });

    return () => {
      unsubscribe();
    };
  }, [navigation]);
};

export default useBackHandling;
