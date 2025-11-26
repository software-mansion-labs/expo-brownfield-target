// Reexport the native module. On web, it will be resolved to ExpoBrownfieldModule.web.ts
// and on native platforms to ExpoBrownfieldModule.ts
// export { default } from './ExpoBrownfieldModule';
// export { default as ExpoBrownfieldModuleView } from './ExpoBrownfieldModuleView';
// export * from './ExpoBrownfieldModule.types';
import ExpoBrownfieldModule from './ExpoBrownfieldModule';

export const hello = () => {
  return ExpoBrownfieldModule?.hello() ?? '';
};
