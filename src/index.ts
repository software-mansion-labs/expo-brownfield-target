import ExpoBrownfieldModule from './ExpoBrownfieldModule';

export const popToNative = (animated: boolean = false): void => {
  return ExpoBrownfieldModule?.popToNative(animated);
};
