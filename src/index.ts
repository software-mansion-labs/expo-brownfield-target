import ExpoBrownfieldModule from './ExpoBrownfieldModule';

export const popToNative = (animated: boolean = false): void => {
  return ExpoBrownfieldModule?.popToNative(animated);
};

export const setNativeBackEnabled = (enabled: boolean): void => {
  return ExpoBrownfieldModule?.setNativeBackEnabled(enabled);
};

export const sendMessage = <T extends object>(message: T) => {
  return ExpoBrownfieldModule?.sendMessage(message);
};
