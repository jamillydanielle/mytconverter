import { NextRequest, NextResponse } from "next/server";
import jwt from "jsonwebtoken";
import { User } from "@/types";
import { DecodedToken } from "@/utils/jwtDecoder";

export function middleware(req: NextRequest) {
  const token = req.cookies.get("token")?.value;
  const currentPath = req.nextUrl.pathname;


  // Se a requisição for para um asset público ou a rota raiz, permite o acesso
  if (/\.(jpg|jpeg|png|gif|svg)$/.test(currentPath) || currentPath === "/") {
    return NextResponse.next();
  }

  // Usuário não autenticado: Acesso apenas a /register, /login e /users/createUser
  if (!token) {
    if (currentPath === "/register" || currentPath === "/login" || currentPath === '/users/users/createUser') {
      return NextResponse.next();
    } else {
      return NextResponse.redirect(new URL("/login", req.url));
    }
  }

  let userType: string | undefined;
  try {
    const decodedToken = jwt.decode(token) as DecodedToken;

    if (!decodedToken || !decodedToken.user) {
        console.error("Token inválido ou sem informações do usuário.");
        return NextResponse.redirect(new URL("/login", req.url));
    }


    userType = decodedToken.scope?.toUpperCase();

    if (!userType) {
      throw new Error("Tipo de usuário não encontrado no token");
    }

  } catch (error) {
    return NextResponse.redirect(new URL("/login", req.url));
  }

  // Controle de acesso baseado no tipo de usuário
  if (userType === "ADMIN") {
    // ADMIN tem acesso a tudo, então não há restrições aqui.
    return NextResponse.next(); // Permitir acesso
  } else if (userType === "USER") {
    if (currentPath.startsWith("/users")) {
      return NextResponse.redirect(new URL("/myconversions", req.url));
    }
    return NextResponse.next(); // Permitir acesso a outras rotas
  } else {
    // Tipo de usuário inválido
    return NextResponse.redirect(new URL("/myconversions", req.url));
  }
}

export const config = {
  matcher: [
    "/((?!api|_next/static|_next/image|favicon.ico|sitemap.xml|robots.txt).*)",
  ],
};