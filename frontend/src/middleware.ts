import { NextRequest, NextResponse } from "next/server";
import jwt from "jsonwebtoken";
import { User } from "@/types";
import { DecodedToken, UserData } from "@/utils/jwtDecoder";

export function middleware(req: NextRequest) {
  const token = req.cookies.get("token")?.value;
  const currentPath = req.nextUrl.pathname;

  console.log("Current path:", currentPath);

  // Se a requisição for para um asset público ou a rota raiz, permite o acesso
  if (/\.(jpg|jpeg|png|gif|svg)$/.test(currentPath) || currentPath === "/") {
    return NextResponse.next();
  }

  // Usuário não autenticado: Acesso apenas a /listaconvertion e /login
  if (!token) {
    if (currentPath === "/convertion" || currentPath === "/login") {
      console.log(`Permitindo acesso a ${currentPath} para usuário não autenticado`);
      return NextResponse.next();
    } else {
      console.log("Redirecionando usuário não autenticado para /listaconvertion");
      return NextResponse.redirect(new URL("/listaconvertion", req.url));
    }
  }

  let userType: string | undefined;
  try {
    const decodedToken = jwt.decode(token) as DecodedToken;

    if (!decodedToken || !decodedToken.user) {
        console.error("Token inválido ou sem informações do usuário.");
        return NextResponse.redirect(new URL("/login", req.url));
    }
    const userData: UserData = JSON.parse(decodedToken.user);


    userType = decodedToken.scope?.toUpperCase();

    if (!userType) {
      throw new Error("Tipo de usuário não encontrado no token");
    }

    console.log("User type:", userType);
  } catch (error) {
    console.error("Erro ao decodificar o token:", error);
    return NextResponse.redirect(new URL("/login", req.url));
  }

  // Controle de acesso baseado no tipo de usuário
  if (userType === "ADMIN") {
    // ADMIN tem acesso a tudo, então não há restrições aqui.
    console.log("Admin acessando:", currentPath);
    return NextResponse.next(); // Permitir acesso
  } else if (userType === "USER") {
    if (currentPath.startsWith("/users")) {
      console.log("Acesso negado a /users para USER, redirecionando para /convertion");
      return NextResponse.redirect(new URL("/convertion", req.url));
    }
    console.log("User acessando:", currentPath);
    return NextResponse.next(); // Permitir acesso a outras rotas
  } else {
    // Tipo de usuário inválido
    console.log("Tipo de usuário inválido, redirecionando para /convertion");
    return NextResponse.redirect(new URL("/convertion", req.url));
  }
}

export const config = {
  matcher: [
    "/((?!api|_next/static|_next/image|favicon.ico|sitemap.xml|robots.txt).*)",
  ],
};