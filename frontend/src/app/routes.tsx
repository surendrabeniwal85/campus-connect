import { moduleRegistry, type ModuleRoute } from './moduleRegistry';

export type RegisteredView = NonNullable<ModuleRoute['view']>;

export function resolveRegisteredView(pathname: string): RegisteredView | null {
  for (const module of moduleRegistry) {
    const route = module.routes.find((candidate) => candidate.path === pathname);
    if (route && 'view' in route) return route.view;
  }
  return null;
}
