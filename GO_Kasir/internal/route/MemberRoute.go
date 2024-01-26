package route

import (
	"github.com/ProjectLSP/internal/controller/Member"
	"github.com/ProjectLSP/internal/middleware"
	"github.com/gofiber/fiber/v2"
)

func MemberRoute(c fiber.Router) {
	app := c.Group("/members")
	app.Use(middleware.APIKeyAuthMiddlewareAdmin)
	app.Get("/", Member.Index)
	app.Post("/", Member.Create)
	app.Get("/:id", Member.Detail)
	app.Put("/:id", Member.Update)
	app.Delete("/:id", Member.Delete)
}
